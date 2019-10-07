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

echo wait...

cd %parent_path%

::Kill self
title=energyForecastStarter
FOR /F "tokens=2" %%p in ('"tasklist /v /NH /FI "windowtitle eq energyForecastStarter""') DO taskkill /pid %%p > NUL 2>&1
