import matplotlib.pyplot as plt

with open('results', 'r') as file:
    lines = file.readlines()

float_lists = []

for line in lines:
    float_list = [float(x) for x in line.split()]
    float_lists.append(float_list)
    # lambdas, exTheta1Hs, acTheta1Hs, exTheta1Ls, acTheta1Ls, exTheta2Hs
    # acTheta2Hs, exTheta2Ls, acTheta2Ls, exN1Hs, acN1Hs, exN1Ls, acN1Ls,
    # exN2Hs, acN2Hs, exN2Ls, acN2Ls, exT2Hs, acT2Hs, exT2Ls, acT2Ls

lambdas = float_lists[0]
exTheta1Hs = float_lists[1]
acTheta1Hs = float_lists[2]
exTheta1Ls = float_lists[3]
acTheta1Ls = float_lists[4]
exTheta2Hs = float_lists[5]
acTheta2Hs = float_lists[6]
exTheta2Ls = float_lists[7]
acTheta2Ls = float_lists[8]
exN1Hs = float_lists[9]
acN1Hs = float_lists[10]
exN1Ls = float_lists[11]
acN1Ls = float_lists[12]
exN2Hs = float_lists[13]
acN2Hs = float_lists[14]
exN2Ls = float_lists[15]
acN2Ls = float_lists[16]
exT2Hs = float_lists[17]
acT2Hs = float_lists[18]
exT2Ls = float_lists[19]
acT2Ls = float_lists[20]

fig, ax = plt.subplots()

ax.scatter(lambdas, exTheta1Hs, label='Expected Throughput of High Priority Customers in Queue 1', color='red', marker='o')
ax.scatter(lambdas, acTheta1Hs, label='Actual Throughput of High Priority Customers in Queue 1', color='blue', marker='s')
ax.scatter(lambdas, exTheta1Ls, label='Expected Throughput of Low Priority Customers in Queue 1', color='darkred', marker='o')
ax.scatter(lambdas, acTheta1Ls, label='Actual Throughput of Low Priority Customers in Queue 1', color='darkblue', marker='s')
ax.set_xlabel('λ')
ax.set_ylabel('E[θ_1]')
ax.legend()

plt.show()

fig, ax = plt.subplots()

ax.scatter(lambdas, exTheta2Hs, label='Expected Throughput of High Priority Customers in Queue 2', color='red', marker='o')
ax.scatter(lambdas, acTheta2Hs, label='Actual Throughput of High Priority Customers in Queue 2', color='blue', marker='s')
ax.scatter(lambdas, exTheta2Ls, label='Expected Throughput of Low Priority Customers in Queue 2', color='darkred', marker='o')
ax.scatter(lambdas, acTheta2Ls, label='Actual Throughput of Low Priority Customers in Queue 2', color='darkblue', marker='s')
ax.set_xlabel('λ')
ax.set_ylabel('E[θ_2]')
ax.legend()

plt.show()

fig, ax = plt.subplots()

ax.scatter(lambdas, exN1Hs, label='Expected Number of High Priority Customers in Queue 1', color='red', marker='o')
ax.scatter(lambdas, acN1Hs, label='Actual Number of High Priority Customers in Queue 1', color='blue', marker='s')
ax.scatter(lambdas, exN2Hs, label='Expected Number of High Priority Customers in Queue 2', color='darkred', marker='o')
ax.scatter(lambdas, acN2Hs, label='Actual Number of High Priority Customers in Queue 2', color='darkblue', marker='s')
ax.set_xlabel('λ')
ax.set_ylabel('E[N]')
ax.legend()

plt.show()

fig, ax = plt.subplots()

ax.scatter(lambdas, exT2Hs, label='Expected Time of High Priority Customers in Queue 2', color='red', marker='o')
ax.scatter(lambdas, acT2Hs, label='Actual Time of High Priority Customers in Queue 2', color='blue', marker='s')
ax.scatter(lambdas, exT2Ls, label='Expected Time of Low Priority Customers in Queue 2', color='darkred', marker='o')
ax.scatter(lambdas, acT2Ls, label='Actual Time of Low Priority Customers in Queue 2', color='darkblue', marker='s')
ax.set_xlabel('λ')
ax.set_ylabel('E[𝜏]')
ax.legend()

plt.show()