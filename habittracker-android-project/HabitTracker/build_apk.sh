#!/bin/bash

# 简单的APK构建脚本

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/opt/android-sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH

cd /workspace/projects/workspace/HabitTracker

# 创建调试keystore
keytool -genkey -v -keystore debug.keystore -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass android -keypass android -dname "CN=Android Debug,O=Android,C=US"

# 使用gradle构建debug APK
./gradlew clean assembleDebug --no-daemon --no-build-cache --offline || {
  echo "Offline build failed, trying with network..."
  ./gradlew clean assembleDebug --no-daemon
}

if [ -f app/build/outputs/apk/debug/app-debug.apk ]; then
  echo "✅ APK 构建成功！"
  echo "📍 路径: app/build/outputs/apk/debug/app-debug.apk"
  ls -lh app/build/outputs/apk/debug/app-debug.apk
else
  echo "❌ APK 构建失败"
fi
