# Compliant usage
with open("compliant.txt", "w") as f:
    f.write("Safe open")

# Non-compliant usage (should be flagged by AvoidFileOpenWithoutWithCheck)
f = open("non_compliant.txt", "w")
f.write("Unsafe open")
f.close()

# Flagged by CustomPythonSubscriptionCheck (subscription)
for i in range(10):
    pass
