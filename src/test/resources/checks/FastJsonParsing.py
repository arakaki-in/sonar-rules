import json
import orjson

# Compliant cases
data1 = orjson.loads('{"a": 1}')
data2 = orjson.dumps({"a": 1})

# Non-compliant cases
data3 = json.loads('{"a": 1}')  # Noncompliant {{Use faster JSON parsing libraries like 'orjson' or 'ujson' instead of the standard 'json' library for high-performance JSON operations.}}
data4 = json.dumps({"a": 1})  # Noncompliant {{Use faster JSON parsing libraries like 'orjson' or 'ujson' instead of the standard 'json' library for high-performance JSON operations.}}
