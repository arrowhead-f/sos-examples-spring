#!/bin/bash

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path"

time_to_sleep=10s

echo Starting Energy Forecast Demo Clients

cd demo-energy-forecast-indoor-provider/target
nohup java -jar $(find . -maxdepth 1 -name demo-energy-forecast-indoor-provider-\*.jar | sort | tail -n1) &
echo indoor-provider started


cd ../../demo-energy-forecast-outdoor-provider/target
nohup java -jar $(find . -maxdepth 1 -name demo-energy-forecast-outdoor-provider-\*.jar | sort | tail -n1) &
echo outdoor-provider started

cd ../../demo-energy-forecast-provider/target
nohup java -jar $(find . -maxdepth 1 -name demo-energy-forecast-provider-\*.jar | sort | tail -n1) &
echo energy-forecast-provider started

sleep ${time_to_sleep}
echo wait..

cd ../../demo-energy-forecast-consumer/target
java -jar $(find . -maxdepth 1 -name demo-energy-forecast-consumer-\*.jar | sort | tail -n1)
