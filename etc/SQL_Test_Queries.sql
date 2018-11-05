# query used for blocking ip
SELECT ip, count(ip) AS accessCount FROM log.access
WHERE date BETWEEN '2017-01-01.00:00:00' and '2017-01-01.23:59:59'
GROUP BY ip
HAVING accessCount >= 200

# query used for getting access logs on ip
SELECT * FROM log.access where ip = '192.168.234.82';