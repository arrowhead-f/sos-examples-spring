@ECHO OFF

echo Shutting down Energy Forecast Demo Clients

SET time_to_sleep=5

FOR /F "tokens=1" %%p in ('"jps -v | find "demo-energy-forecast-indoor-provider""') DO taskkill /pid %%p > NUL 2>&1
FOR /F "tokens=1" %%p in ('"jps -v | find "demo-energy-forecast-outdoor-provider""') DO taskkill /pid %%p > NUL 2>&1
FOR /F "tokens=1" %%p in ('"jps -v | find "demo-energy-forecast-provider""') DO taskkill /pid %%p > NUL 2>&1

timeout /t 2 /nobreak > NUL
SET STILL_THERE=""

FOR /F "tokens=1" %%p in ('"jps -v | find "demo-energy-forecast-indoor-provider""') DO set STILL_THERE=%%p

IF "%STILL_THERE%"=="""" (
  echo Energy Forecast Demo Clients killed
) ELSE (
  FOR /F "tokens=1" %%p in ('"jps -v | find "demo-energy-forecast-indoor-provider""') DO taskkill /F /pid %%p
  FOR /F "tokens=1" %%p in ('"jps -v | find "demo-energy-forecast-outdoor-provider""') DO taskkill /F /pid %%p
  FOR /F "tokens=1" %%p in ('"jps -v | find "demo-energy-forecast-provider""') DO taskkill /F /pid %%p
  echo Energy Forecast Demo Clients forcefully killed
)
