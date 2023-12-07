# Nicholas Colvin
# nxc220016

import matplotlib.pyplot as plt

with open('data/results', 'r') as file:
    lines = file.readlines()

# lambdas, exTheta1Hs, acTheta1Hs, exTheta1Ls, acTheta1Ls, exTheta2Hs
# acTheta2Hs, exTheta2Ls, acTheta2Ls, exN1Hs, acN1Hs, exN1Ls, acN1Ls,
# exN2Hs, acN2Hs, exN2Ls, acN2Ls, exT2Hs, acT2Hs, exT2Ls, acT2Ls
num_vars = 21
float_lists = [[] for _ in range(num_vars)]

for line in lines:
    values = [float(x) for x in line.split()]
    for i in range(num_vars):
        float_lists[i].append(values[i])

lambdas, exTheta1Hs, acTheta1Hs, exTheta1Ls, acTheta1Ls, exTheta2Hs, \
acTheta2Hs, exTheta2Ls, acTheta2Ls, exN1Hs, acN1Hs, exN1Ls, acN1Ls, \
exN2Hs, acN2Hs, exN2Ls, acN2Ls, exT2Hs, acT2Hs, exT2Ls, acT2Ls = float_lists

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
ax.scatter(lambdas, exN1Ls, label='Expected Number of Low Priority Customers in Queue 1', color='darkred', marker='o')
ax.scatter(lambdas, acN1Ls, label='Actual Number of Low Priority Customers in Queue 1', color='darkblue', marker='s')
ax.set_xlabel('λ')
ax.set_ylabel('E[N_1]')
ax.legend()

plt.show()

fig, ax = plt.subplots()

ax.scatter(lambdas, exN2Hs, label='Expected Number of High Priority Customers in Queue 2', color='red', marker='o')
ax.scatter(lambdas, acN2Hs, label='Actual Number of High Priority Customers in Queue 2', color='blue', marker='s')
ax.scatter(lambdas, exN2Ls, label='Expected Number of Low Priority Customers in Queue 2', color='darkred', marker='o')
ax.scatter(lambdas, acN2Ls, label='Actual Number of Low Priority Customers in Queue 2', color='darkblue', marker='s')
ax.set_xlabel('λ')
ax.set_ylabel('E[N_2]')
ax.legend()

plt.show()

fig, ax = plt.subplots()

ax.scatter(lambdas, exT2Hs, label='Expected Time of High Priority Customers in Queue 2', color='red', marker='o')
ax.scatter(lambdas, acT2Hs, label='Actual Time of High Priority Customers in Queue 2', color='blue', marker='s')
ax.scatter(lambdas, exT2Ls, label='Expected Time of Low Priority Customers in Queue 2', color='darkred', marker='o')
ax.scatter(lambdas, acT2Ls, label='Actual Time of Low Priority Customers in Queue 2', color='darkblue', marker='s')
ax.set_xlabel('λ')
ax.set_ylabel('E[T_2]')
ax.legend()

plt.show()