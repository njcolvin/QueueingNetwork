import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

with open('data/states', 'r') as file:
    lines = file.readlines()

points = []

for line in lines:
    row = [float(x) for x in line.split()]
    points.append(row)

h1 = [row[0] for row in points]
l1 = [row[1] for row in points]
h2 = [row[2] for row in points]
l2 = [row[3] for row in points]

# Normalize the color values to be within [0.0, 1.0]
color = (l2 - np.min(l2)) / (np.max(l2) - np.min(l2))

# Set up the figure and axis
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Set labels for axes
ax.set_xlabel('H1')
ax.set_ylabel('L1')
ax.set_zlabel('H2')

# Create a colorbar
sc = plt.cm.ScalarMappable(cmap='plasma', norm=plt.Normalize(vmin=np.min(l2), vmax=np.max(l2)))
sc.set_array(color)
cbar = plt.colorbar(sc, ax=ax, pad=0.1)
cbar.set_label('L2')

lines = []
window_size = 100

# Function to initialize the plot
def init():
    # Create an empty line
    line, = ax.plot([h1[0], h1[1]], [l1[0], l1[1]], [h2[0], h2[1]], lw=2)
    lines.append(line)
    line.set_color(plt.cm.plasma(color[1]))
    return line,

# Function to update the plot for each frame
def update(frame):
    line, = ax.plot([h1[frame], h1[frame + 1]], [l1[frame], l1[frame + 1]], [h2[frame], h2[frame + 1]], lw=2)
    lines.append(line)
    line.set_color(plt.cm.plasma(color[frame + 1]))

    if frame > window_size:
        ax.lines[0].remove()

    # Set the title to show the current frame/time
    ax.set_title('Frame: {}'.format(frame))

    return line,

# Create the animation
num_frames = len(h1) - 1
ani = FuncAnimation(fig, update, frames=num_frames, init_func=init, interval=5, blit=False, repeat=False)

# Show the animation
plt.show()