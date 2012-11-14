const Cc = Components.classes;
const Ci = Components.interfaces;
const Cu = Components.utils;

Cu.import("resource://gre/modules/XPCOMUtils.jsm");
Cu.import("resource://gre/modules/Services.jsm");

var consoleService = Components.classes["@mozilla.org/consoleservice;1"]
	.getService(Components.interfaces.nsIConsoleService);

consoleService.logStringMessage("Web SQL starting up!");

(function(globalObj) {

	// used to serialize transactions
	var transactionInProgress = false;
	var nextTransactionId = 1;
	
	function Database(dbName, dbVersion, dbDescription, dbSize, window)
	{
		// Use some parts from the client's Window object.
		var windowLocation = XPCNativeWrapper.unwrap(window.location);

		// useful for debugging
		var alert = XPCNativeWrapper.unwrap(window.alert);
		this.window = window;
		
		// Store the database file in {Profile Directory}/databases/{hostname}/{dbName}.sqlite
		var file = Cc["@mozilla.org/file/directory_service;1"]
						 .getService(Ci.nsIProperties)
						 .get("ProfD", Ci.nsIFile);
		file.append("databases");
		try{ file.create(Ci.nsIFile.DIRECTORY_TYPE, 0700); } catch (e) {}
		
		file.append(windowLocation.protocol.replace(':','') + "_" + windowLocation.hostname + "_" + windowLocation.port);
		try{ file.create(Ci.nsIFile.DIRECTORY_TYPE, 0700); } catch (e) {}
		
		file.append(dbName + ".sqlite");
		
		window.console.log('openDatabase(): file = ' + file);		

		
		// don't open the database immediately, that will prevent javascript
		// in other windows from opening the same database. Multiple callers should
		// be able to use the same database, with their respective transactions queued (TODO)
		var storageService = Cc["@mozilla.org/storage/service;1"]
			.getService(Ci.mozIStorageService);
		
		var openPhysicalDatabase = function() {
			return storageService.openDatabase(file); // Will also create the file if it does not exist;
		};
		
		var queueTask = function(fn) {
			window.setTimeout(fn, 0);
		};
		
		// SQLError codes
		var UNKNOWN_ERR = 0;
		var DATABASE_ERR = 1;
		var VERSION_ERR = 2;
		var TOO_LARGE_ERR = 3;
		var QUOTA_ERR = 4;
		var SYNTAX_ERR = 5;
		var CONSTRAINT_ERR = 6;
		var TIMEOUT_ERR = 7;
		
		function SQLTransaction(db, errorCallback, successCallback) {
			this.db = db;
			this.errorCallback = errorCallback;
			this.successCallback = successCallback;
			this.aborted = false;
			this.queue = [];
			this.id = nextTransactionId++;
			
			// create an opaque wrapper around this object that 
			// we can pass to the client
			var self = this;
			this.wrapper = {
				executeSql: function (sqlStatement, arguments, callbackFunc, errorFunc) {
					return self.executeSql(sqlStatement, arguments, callbackFunc, errorFunc);
				}
			};
		};
		
		SQLTransaction.prototype = {
				
			begin: function(callback) {
				
				transactionInProgress = true;
				
				// 4. If the transaction callback is not null, queue a task to invoke the transaction callback with 
				// the aforementioned SQLTransaction object as its only argument, and wait for that task to be run.
				var self = this;
				self.trace('begin: starting...');
				queueTask(function() {
					try {
						self.trace('begin: calling callback...');
						callback(self);
						self.scheduleQueue();
					} catch(e) {
						// 5. If the callback raised an exception, jump to the last step.
						self.fail(self.translateError(e));
					}
				});
			},
			
			enqueue: function(taskFn) {
				this.queue.push(taskFn);
			},
			
			scheduleQueue: function() {
				var self = this;
				self.trace('scheduleQueue: called, queue size = ' + self.queue.length);
				queueTask(function() {
					self.processQueue();
				});
			},
						
			processQueue: function() {
				// 6. While there are any statements queued up in the transaction, perform the following steps 
				// for each queued up statement in the transaction, oldest first. Each statement has a statement, 
				// optionally a result set callback, and optionally an error callback.

				if(!this.aborted) {
					this.trace('processQueue: processing tx queue, ' + this.queue.length + ' tasks remaining');
					var nextTask = this.queue.shift();
					if(!nextTask) {
						this.commit();
					} else {
						nextTask();
					}
				}
			},
			
			executeSql:	function (sqlStatement, params, callbackFunc, errorFunc) {
				var self = this;

				this.enqueue(function() {
					
					//self.trace('executeSql: executing statement ' + sqlStatement);
					self.trace("executeSql: errorFunc = " + (typeof errorFunc));
					
					// 6.1 If the statement is marked as bogus, jump to the "in case of error" steps below.
					// (TODO)
					
					// 6.2 Execute the statement in the context of the transaction. [SQL]
					var parsedStatementParts = sqlStatement.split('?');
					var parsedStatement = '';
					// use named parameters rather than positional params
					// there seems to be a bug with positional params when used with INSERT
					// statements...
					for (var i = 1; i < parsedStatementParts.length; i++)
						parsedStatement += parsedStatementParts[i - 1] + ':p' + i;
					parsedStatement += parsedStatementParts[parsedStatementParts.length - 1];
					self.trace('parsedStatement = ' + parsedStatement);
					
					var statement;
					try {
						statement = self.db.createStatement(parsedStatement);
						
						if (params && params.length) {
							var bindingArray = statement.newBindingParamsArray();
							var bp = bindingArray.newBindingParams();
							for (var i = 0; i < params.length; i++)
								bp.bindByName('p'+(i+1), params[i]);
							bindingArray.addParams(bp);
							statement.bindParameters(bindingArray);
						}
					} catch (e) {
						self.trace('executeSql: db.createStatement() failed: ' + e);
						self.handleStatementError(errorFunc, self.translateError(e));
						return;
					}
					
					// 6.2 Execute the statement in the context of the transaction
					var rows = [];
					statement.executeAsync({
						handleError: function(aError) {
							// 6.3. If the statement failed, jump to the "in case of error" steps below.
							self.handleStatementError(errorFunc, self.translateError(aError));
						},
						
						handleResult: function(aResultSet) {
							self.trace('handleResult() called.');
							
							var nextRow;
							while (nextRow = aResultSet.getNextRow()) {
								var row = {};
								for (var i = 0; i < statement.columnCount; i++)
								{
									var colName = statement.getColumnName(i);
									row[colName] = nextRow.getResultByIndex(i);
								}
								rows.push(row);
							}
						},
						
						handleCompletion: function(aReason) {
							self.trace('handleCompletion() called: aReason = '  + aReason);
							if (aReason == 0) {
								// TODO find out how to get the number of rows affected by the query
								var rs = { 'insertId':self.db.lastInsertRowID, 'rowsAffected':-1, 'rows':rows };
								rs.rows.item = function(i){ return rows[i]; }
								rs.rows.length = rows.length;
								
								self._handleStatementSuccess(rs, callbackFunc);
							}
						}
					});
				});
			},
			
			_handleStatementSuccess: function(rs, callbackFunc) {
				var self = this;
				
				// 5. If the statement has a result set callback that is not null, queue a task to invoke 
				// it with the SQLTransaction object as its first argument and the new SQLResultSet object 
				// as its second argument, and wait for that task to be run.
				self.trace('handleStatementSuccess: num rows = ' + rs.rows.length + 
						', callback: ' + (typeof callbackFunc));
								
				if(typeof callbackFunc == 'function') {
					try {
						callbackFunc(self.wrapper, rs);
					} catch(e) {
						this.trace('handleStatementSucess: callback function threw exception: ' + e);
						this.fail(this.translateError(e));
						return;
					}
				}
				
				this.scheduleQueue();
			},

			trace: function(msg) {
				if(this.window) {
					this.window.console.log('[' + this.id + '] ' + msg);					
				}
			},
			
			translateError: function(e) {
				//TODO
				return { message: (''+e), code: UNKNOWN_ERR };
			},
			
			handleStatementError: function (statementErrHandler, sqlError) {
				// 6(ERROR). In case of error (or more specifically, if the above substeps say to jump to the
				// "in case of error" steps), run the following substeps:

				this.trace('handleStatementError: entering');

				var abort = true;
				
				// 6(ERROR).1 If the statement had an associated error callback that is not null, 
				// then queue a task to invoke that error callback with the SQLTransaction object 
				// and a newly constructed SQLError object that represents the error that caused 
				// these substeps to be run as the two arguments, respectively, and wait for the task to be run.
				if(typeof statementErrHandler == "function") {
					try {
						abort = statementErrHandler(this.wrapper, sqlError);
						this.trace('handleStatementError: err callback returned ' + (abort ? 'abort' : 'continue'));
					} catch(e) {
						this.trace('handleStatementError: statementErrHandler threw exception: ' + e);
						abort = true;
					};
				} else {
					this.trace('handleStatementError: no statement err handler provided, aborting');

				}
				// 6(ERROR).2 If the error callback returns false, then move on to the next statement, 
				// if any, or onto the next overall step otherwise.

				// 6(ERROR).3 Otherwise, the error callback did not return false, or there was no error callback. 
				// Jump to the last step in the overall steps.

				this.trace((abort ? 'aborting' : 'continuing') + ' transaction');
				if(abort) {
					this.fail(sqlError);
				} else {
					this.scheduleQueue();
				}
			},
			
			commit: function() {
				var self = this;
				self.trace('committing transaction...');
				// 7. If a postflight operation was defined for this instance of the transaction steps,
				// then: as one atomic operation, commit the transaction and, if that succeeds, run the 
				// postflight operation. If the commit fails, then instead jump to the last step.
				// (This is basically a hook for the changeVersion() method.)
				// (TODO)
				
				// Otherwise: commit the transaction. If an error occurred in the committing of the transaction,
				// jump to the last step.
				try {
					self.db.commitTransaction();
					self.db.asyncClose();
				} catch(e) {
					self.trace('commit: db.commitTransaction() threw exception: ' + e);
					self.fail();
					return;
				}
				
				self.trace('commit: commit succeeded');
				
				// 8. Queue a task to invoke the success callback, if it is not null.
				if(typeof self.successCallback == 'function') {
					queueTask(function() {
						self.trace('commit: executing tx success callback');
						self.successCallback();
					});
				}
				
				// 9. End these steps. The next step is only used when something goes wrong.
				transactionInProgress = false;

			},
			
			fail: function(err) {
				// 10. Queue a task to invoke the transaction's error callback, if it is not null, 
				// with a newly constructed SQLError object that represents the last error to have 
				// occurred in this transaction. Rollback the transaction. Any still-pending statements 
				// in the transaction are discarded.
		
				var self = this;
				self.aborted = true;

				self.trace("fail: starting...");

				
				if(typeof this.errorCallback == 'function') {
					self.trace("fail: scheduling error callback.");

					queueTask(function(){
						self.trace("fail: error calling callback.");
						try {
							self.errorCallback(err);
						} catch(e) {
							self.trace('fail: errorCallback() threw exception: ' + e);
						}	
					});
				}
				try {
					self.trace("fail: rolling back tx...");
					self.db.rollbackTransaction();
					self.trace("fail: tx rolled back");
					self.db.asyncClose();
					self.trace("fail: asyncClose() called");
				} catch(e) { 
					self.trace('fail: db.rollbackTransaction() threw exception: ' + e);
				}
				
				transactionInProgress = false;

			}

		};
		
		this.transaction = function (callback, errorCallback, successCallback) {
			if (typeof callback != 'function')
				throw 'Callback must be a function';
			var self = this;
			
			self.window.console.log('WebSQL: transaction in progress ' + transactionInProgress);

			
			var beginTransaction = function() {
				
				var db = openPhysicalDatabase();
				
				var tx = new SQLTransaction(db, errorCallback, successCallback);
				tx.window = self.window;
				try {
					db.beginTransaction();
				} catch(e) {
					tx.trace('failed to begin transaction ' + e)
					if(typeof errorCallback == 'function') {
						errorCallback(tx, tx.translateError(e));
					}
					return;
				}
				
				tx.begin(callback);
			};
			
			var tryBeginTransaction = function() {
				if(!transactionInProgress) {
					beginTransaction();
				} else {
					self.window.console.log('WebSQL: transaction already progress, scheduling new transaction for later...');
					queueTask(tryBeginTransaction);
				}
			};
			tryBeginTransaction();
		}
		
		this.readTransaction = this.transaction;
		this.changeVersion = function() { /* Who cares about versions? */ }
	}

	function WebSqlFactory() {};
	WebSqlFactory.prototype = {
		classID:		Components.ID("{6F7D3F6A-2E43-11E2-BF28-04D96088709B}"),
		QueryInterface:	XPCOMUtils.generateQI([Ci.nsIDOMGlobalPropertyInitializer]),
		init:			function ws_init(aWindow)
		{
			var window = XPCNativeWrapper.unwrap(aWindow);
			function openDatabase(dbName, dbVersion, dbDescription, dbSize)
			{
				return new Database(dbName, dbVersion, dbDescription, dbSize, window);
			}
			openDatabase.toString = function(){return 'function openDatabase() {\n    [native code]\n}';};
			openDatabase.aiVersion = 1;
			return openDatabase;
		}
	};
	
	globalObj.WebSqlFactory = WebSqlFactory;
})(this);
let NSGetFactory = XPCOMUtils.generateNSGetFactory([WebSqlFactory]);