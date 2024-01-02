import time
import requests
import random
from multiprocessing import Pool

url = "http://localhost:8080"

def send_put_request(num):
    try:
        data = {
            "username": "username" + num,
            "pin": int(num),
            "zarada": random.random() + float(num),
        }
        response = requests.put(url, json=data)
        return response.status_code
    except Exception as e:
        print(e)
        return 500

def send_put_request2(num):
    try:
        data = {
            "username": "username" + num,
            "pin": int(num) * 50,
            "zarada": random.random() + float(num),
        }
        response = requests.put(url, json=data)
        return response.status_code
    except Exception as e:
        print(e)
        return 500

def send_get_request():
    try:
        response = requests.get(url)
        list = response.json()
        list.sort(key=lambda x: x["pin"])
        for i in list:
            print(i["username"], i["pin"], i["zarada"]) #, file=open("output.txt", "a")
        return response.status_code
    except Exception as e:
        print(e)
        return 500
    
def send_get_request_param():
    try:
        response = requests.get(url + "?username=username" + str(random.randint(0, 10000)))
        #print(response.text)
        return response.status_code
    except Exception as e:
        print(e)
        return 500

def main():
    pool = Pool(processes=50)
    #args = [str(i) for i in range(10000)]
    start = time.time()
    #res = pool.map(send_put_request, args)

    for i in range(200000):
        pool.apply_async(send_get_request_param)

    # pool.apply_async(send_get_request)

    pool.close()
    pool.join()
    print(time.time() - start)

if __name__ == "__main__":
    
    main()