@ECHO OFF

SET parent_path=%~dp0
cd %parent_path%

SET time_to_sleep=10

echo Starting Energy Forecast Demo Clients

cd demo-energy-forecast-indoor-provider\target
START "demo-energy-forecast-indoor-provider" /B "cmd /c javaw -jar demo-energy-forecast-indoor-provider-4.1.3.jar 2>&1"
echo indoor-provider started

cd ..\..\demo-energy-forecast-outdoor-provider\target
START "demo-energy-forecast-outdoor-provider" /B "cmd /c javaw -jar demo-energy-forecast-outdoor-provider-4.1.3.jar 2>&1"
echo outdoor-provider started

cd ..\..\demo-energy-forecast-provider\target
START "demo-energy-forecast-provider" /B "cmd /c javaw -jar demo-energy-forecast-provider-4.1.3.jar 2>&1"
echo energy-forecast-provider started

echo wait...
timeout /t %time_to_sleep% /nobreak > NUL

echo energy-forecast-consumer started
cd ..\..\demo-energy-forecast-consumer\target
START "demo-energy-forecast-consumer" /B "cmd /c START java -jar demo-energy-forecast-consumer-4.1.3.jar 2>&1"

::Kill self