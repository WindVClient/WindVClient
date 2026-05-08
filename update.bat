@echo off
setlocal enabledelayedexpansion

:input
set "commit_msg="
set /p "commit_msg=コミットメッセージ（変更内容）を入力してください: "

if "%commit_msg%"=="" (
    echo.
    echo [!] メッセージが空です。もう一度入力してください。
    goto :input
)

echo.
echo === 1. 変更を記録します (Add/Commit) ===
git add .
git commit -m "%commit_msg%"

echo.
echo === 2. 最新の状態を取得して結合します (Pull) ===
git pull origin main --rebase

echo.
echo === 3. アップロードを開始します (Push) ===
git push origin main

echo.
echo === 完了しました！ ===
pause