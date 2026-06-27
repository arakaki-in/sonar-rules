# Compliant code for files
with open("compliant.txt", "w") as f:
    f.write("Safe open")

# 1. AvoidFileOpenWithoutWith
f = open("non_compliant.txt", "w")

# 2. subscription (exactly one 'for' loop)
for i in range(1):
    pass

# 3. NoGlobalMutableState (exactly one global mutable assignment)
global_list = []

def trigger_rules():
    # 4. ThreadLocalUsage
    import threading
    local_data = threading.local()

    # 5. ImmutableDataTransfer
    import queue
    q = queue.Queue()
    q.put([])

    # 6. EnforceConnectionPooling
    import sqlite3
    conn = sqlite3.connect("db.sqlite")

    # 7. MandatoryTimeouts
    import requests
    requests.get("https://example.com")

    # 8. ZeroNPlusOneQueries
    cond = False
    while cond:
        session.query(User).first()

    # 9. AvoidSelectStar
    sql_query = "SELECT * FROM users"

    # 10. BatchOperationsRequired
    while cond:
        session.add(None)

    # 11. DbLevelAggregation
    total = len(session.query(User).all())

    # 12. AvoidEagerRegexCompilation
    import re
    pat = re.compile("abc")

    # 13. GeneratorsOverLists
    val = sum([x for x in []])

    # 14. EfficientStringConcatenation
    str_text = ""
    while cond:
        str_text += "x"

    # 16. FastJsonParsing
    import json
    json.loads("{}")

    # 17. AvoidTryExceptControlFlow
    try:
        x = d["key"]
    except KeyError:
        pass

# 15. UseSlots (class def is not a mutable variable assignment)
class MyUserDTO:
    pass
