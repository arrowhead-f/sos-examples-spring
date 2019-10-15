#!/bin/bash
echo Shutting down Energy Forecast Demo Clients
pkill -f demo-energy-forecast-indoor-provider
pkill -f demo-energy-forecast-outdoor-provider
pkill -f demo-energy-forecast-provider

if pgrep -f sdemo-energy-forecast-indoor-provider
then
  kill -KILL $(ps aux | grep 'demo-energy-forecast-indoor-provider' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'demo-energy-forecast-outdoor-provider' | awk '{print $2}')
  kill -KILL $(ps aux | grep 'demo-energy-forecast-provider' | awk '{print $2}')
  echo Energy Forecast Demo Clients forcefully killed
else
  echo Energy Forecast Demo Clients killed
fi