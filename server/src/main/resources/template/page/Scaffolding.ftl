<#macro scaffolding title>

<!DOCTYPE html>
<html>
<head>
  <title>ActivityInfo - Login</title>
  <!-- CSS Files -->
  <link href="css/bootstrap.css" rel="stylesheet" media="screen">
  <link href="css/bootstrap-responsive.css" rel="stylesheet" media="screen">
  <link href="css/style.css" rel="stylesheet" media="screen">
</head>
<body>
    <header role="banner">
    
      <div id="inner-header" class="clearfix">
        
        <div class="navbar navbar-static-top">
          <div class="navbar-inner">
            <div class="container-fluid nav-container">
              <nav role="navigation">
                <a class="brand" href="http://about.activityinfo.org" style=""><img src="img/logo-shadow.png"><span>ActivityInfo</span></a>
                <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                      <span class="icon-bar"></span>
                      <span class="icon-bar"></span>
                      <span class="icon-bar"></span>
                </a>
               
              </nav>
            </div>
          </div>
  		</div><!-- /.navbar -->
      
      </div> <!-- end #inner-header -->
    
    </header> <!-- end header -->

	<#nested>


	  
</body>
</html>
</#macro>

<#macro content>
    <div class="container-fluid">
		<div id="content" class="clearfix row-fluid">

		<#nested>
		
	  
    	</div> <!-- end #content -->
	</div>
</#macro>

<#macro scripts>
  <script src="http://code.jquery.com/jquery-latest.js"></script>
  <script src="js/bootstrap.min.js"></script>
  <#nested>
</#macro>

<#macro footer>
	<footer role="contentinfo" class="navbar-inner">				
		<div id="inner-footer" class="container clearfix  row">				  
			<div id="widget-footer" class="clearfix row-fluid">
		    	<div class="span3">
			    	<div id="text-4" class="widget widget_text">
			    		<h4>Developed by</h4>			
			    		<div class="textwidget">
			    			<p><a href="http://www.unicef.org" target="_blank">
			    			<img src="http://about.activityinfo.org/wp-content/uploads/2011/10/unicef-logo.png"></a></p>
							<p><a href="http://www.bedatadriven.com" target="_blank"><img src="http://about.activityinfo.org/wp-content/uploads/2011/10/bdd.png"></a></p>
						</div>
					</div>
				</div>
				<div class="span3">
					<div id="text-5" class="widget widget_text">
						<h4>Partners</h4>
						<div class="textwidget"><p><a href="http://www.unocha.org" target="_blank">
							<img src="http://about.activityinfo.org/content/wp-content/uploads/2011/10/OCHA-Logo.png"></a></p>
						</div>
					</div>
				</div>
			    <div class="span3">
				</div>
			    <div class="span3">
					<div id="text-6" class="widget widget_text"><h4>Contact us</h4>
						<div class="textwidget">
							<table cellpadding="2" cellspacing="0" width="100%">
							<tbody><tr>
							<td valign="top">
							<img src="http://activityinfo.dreamhosters.com/content/wp-content/uploads/2011/10/drc-flag-e1319539034502.gif" align="left" width="25" height="16"> </td>
							<td> Cluster use within the DRC: <a href="mailto:pknguessan@unicef.org"> pknguessan@unicef.org</a></td>
							</tr>
							<tr>
							<td valign="top"><img src="http://activityinfo.dreamhosters.com/content/wp-content/uploads/2011/10/globe-ico.png" align="left"></td>
							<td> Using ActivityInfo within your cluster or for your organisation: <a href="mailto:cbarnhoorn@bedatadriven.com"> cbarnhoorn@bedatadriven.com</a></td>
							</tr>
							<tr>
							<td valign="top"><img src="http://activityinfo.dreamhosters.com/content/wp-content/uploads/2011/10/help-icon.png" align="left"></td>
							<td> For help and general questions: <a href="mailto:activityinfo@bedatadriven.com">activityinfo@bedatadriven.com</a></td>
							</tr></tbody>
							</table> 
						</div>
					</div>
				</div>
			</div>
			<nav class="clearfix"></nav>
													
		</div> <!-- end #inner-footer -->
					
	</footer>
</#macro>