#!/bin/bash

# Android APK构建脚本（简化版）

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export ANDROID_HOME=/opt/android-sdk
export PATH=$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH

PROJECT_DIR="/workspace/projects/workspace/HabitTracker"
cd "$PROJECT_DIR"

echo "=========================================="
echo "  Android APK 构建尝试 #3"
echo "=========================================="
echo ""

# 检查环境
echo "【1/5】检查环境..."
echo "Java: $JAVA_HOME"
java -version 2>&1 | head -1
echo "Android SDK: $ANDROID_HOME"
echo "Platform: $(ls $ANDROID_HOME/platforms/)"
echo "Build Tools: $(ls $ANDROID_HOME/build-tools/)"
echo ""

# 清理
echo "【2/5】清理缓存..."
rm -rf .gradle build app/build .idea
echo "清理完成"
echo ""

# 尝试简单编译
echo "【3/5】尝试简单任务..."
./gradlew tasks --no-daemon --quiet 2>&1 | head -10 &
PID=$!

# 等待最多60秒
for i in {1..60}; do
    if ! kill -0 $PID 2>/dev/null; then
        echo "任务完成"
        break
    fi
    echo -n "."
    sleep 1
done
echo ""

# 如果还在运行，终止它
if kill -0 $PID 2>/dev/null; then
    echo "任务超时，终止..."
    kill -9 $PID 2>/dev/null
    wait $PID 2>/dev/null
    echo "已终止"
fi

# 检查是否有生成的文件
echo "【4/5】检查构建产物..."
find . -name "*.apk" -type f 2>/dev/null | head -5
echo ""

# 检查下载的依赖
echo "【5/5】检查依赖缓存..."
find ~/.gradle/caches -name "*.jar" 2>/dev/null | wc -l
echo ""

echo "=========================================="
echo "  构建尝试完成"
echo "=========================================="
