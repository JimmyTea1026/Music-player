import socket

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

server_address = ('192.168.68.59', 8888)  
client_socket.connect(server_address)

cmd = ""
while(True):
    cmd = input("Enter command: ")
    client_socket.sendall(("%s\n"%cmd).encode('utf-8'))
    
    if cmd == "exit":
        break


client_socket.close()