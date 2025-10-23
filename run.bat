@echo off
REM Windows Batch Script to Run Student Attendance Management System

echo Compiling Java files...
javac -d bin src\com\attendance\model\*.java src\com\attendance\manager\*.java src\com\attendance\ui\*.java

if %errorlevel% equ 0 (
    echo Compilation successful!
    echo Running Student Attendance Management System...
    echo.
    java -cp bin com.attendance.ui.AttendanceSystemUI
) else (
    echo Compilation failed!
    pause
    exit /b 1
)
