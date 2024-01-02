import matplotlib.pyplot as plt

# Sample data
#x = ["start", "1", "10", "100", "1000", "10000", "100000"]
# JAVA
# y = [19.1, 53.1, 55.2, 64.2, 122.3, 336, 449.8]
# Rust
#y = [131.1, 131.1, 131.1, 131.1, 262.1, 1400, 14000, 196700]
# Rust neoptimizirano
#y = [131.1, 131.1, 131.1, 131.1, 262.1, 1400, 14000, 196700]
# Plotting the data
x = []
y = []
with open("rust.csv", "r") as f:
    splited = f.read().split(",")
    for i in range(len(splited)):
        if(splited[i] == ""):
            continue
        x.append(i)
        y.append(int(splited[i]))

plt.plot(x, y, label='Rust speed 100 requests')

# Adding labels and title
plt.xlabel('Data samples')
plt.ylabel('Time in micros')
plt.title('Rust speed diagram')
# Adding a legend
plt.legend()

# for i, txt in enumerate(y):
#     plt.annotate(str(txt), (x[i], y[i]), textcoords="offset points", xytext=(0,10), ha='center')
# Display the plot
plt.show()
#Time for 100 requests: 216ms