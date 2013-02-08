#!/bin/sh

# Download the base ISO image

if [ ! -f turnkey-tomcat-11.3-lucid-x86.iso ];
then
	wget http://downloads.sourceforge.net/project/turnkeylinux/iso/turnkey-tomcat-11.3-lucid-x86.iso
fi

sudo rm -rf *.cdroot *.rootfs

if [ ! -d  patch/overlay/root ]; then
  mkdir -p patch/overlay/root
fi

if [ ! -d patch/overlay/var/lib/tomcat/server/lib ]; then
  mkdir -p patch/overlay/var/lib/tomcat/server/lib
fi

cp ../server/target/activityinfo-server-*/WEB-INF/lib/mail-*.jar patch/overlay/var/lib/tomcat/server/lib

cp ../server/target/activityinfo-server-*.war patch/overlay/root/ai.war

# Clean up old build files

if [ -f patch.tar.gz ];
then
	rm patch.tar.gz
fi

export TKLPATCH_DEBUG=1

# Execute
sudo tklpatch turnkey-tomcat-11.3-lucid-x86.iso patch

if [ -f turnkey-tomcat-11.3-lucid-x86-patched.iso ]; then
  mv turnkey-tomcat-11.3-lucid-x86-patched.iso activityinfo-appliance.iso
fi


