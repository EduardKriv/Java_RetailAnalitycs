CREATE DATABASE retail_analytics;

-- ATTENTION!!!
-- Before importing make sure all '*.csv' '*.tvc' files are copied/moved from './datasets/' to Postgres data folder!
-- WINDOWS (path example: 'C:/Program Files/PostgreSQL/14/data/')
-- Darwin (path example: '/var/lib/postgresql/12/main/')

-- Find the dir:
SHOW data_directory;

SHOW datestyle;
SET datestyle TO DMY;
SHOW datestyle;
