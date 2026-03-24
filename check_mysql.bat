@echo off
echo Checking MySQL connection...
powershell -Command "Test-NetConnection -ComputerName localhost -Port 3306"
if %errorlevel% neq 0 (
    echo [ERROR] MySQL is NOT running on port 3306!
    echo Please start your MySQL server (XAMPP or Workbench).
) else (
    echo [SUCCESS] MySQL is running on port 3306.
)
pause

