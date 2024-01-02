use std::collections::HashMap;
use std::io::{Read, Write};
use std::net::{TcpListener, TcpStream};
use std::sync::{Arc, RwLock};
use std::thread;
mod user;
use user::User;

fn handle_client(mut stream: TcpStream, map: Arc<RwLock<HashMap<String, User>>>) {
    let mut buffer = [0; 2048];
    stream.read(&mut buffer).unwrap();

    let request = String::from_utf8_lossy(&buffer[..]);
    let response = if request.starts_with("GET") {
        handle_get(request.to_string(), map)
    } else if request.starts_with("PUT") {
        handle_put(request.to_string(), map)
    } else {
        "HTTP/1.1 400 Bad Request\r\n\r\nInvalid request".to_string()
    };

    stream.write_all(response.as_bytes()).unwrap();
    stream.flush().unwrap();
}

fn handle_get(request: String, map: Arc<RwLock<HashMap<String, User>>>) -> String {
    let username_index = request.find("?username=").unwrap_or(0);
    let return_data: String;
    if username_index != 0 {
        let username_index = username_index + 10;
        let username_end_index = &request[username_index..].find(' ').unwrap_or(0);
        let username = &request[username_index..=username_index + *username_end_index - 1];
        let map = map.read().unwrap();
        if let Some(user) = map.get(username) {
            return_data = serde_json::to_string_pretty(&user).unwrap();
        } else {
            return format!("HTTP/1.1 404 Not Found\r\n\r\nUser '{}' not found", username);
        }
    } else {
        let map = map.read().unwrap();
        let values: Vec<_> = map.values().collect();
        return_data = serde_json::to_string_pretty(&values).unwrap();
    }
    
    
    format!(
        "HTTP/1.1 200 OK\r\n\r\n{}", return_data
    )
}

fn handle_put(request: String, map: Arc<RwLock<HashMap<String, User>>>) -> String {
    let data_start = request.find('{').unwrap_or(0);
    let data_end = request.rfind('}').unwrap_or(0);
    if data_start == 0 || data_end == 0 {
        return format!("HTTP/1.1 400 Bad Request\r\n\r\nInvalid request");
    }
    let data = &request[data_start..=data_end];
    match serde_json::from_str::<User>(data) {
        Ok(new_data) => {
            {
                let mut map = map.write().unwrap();
                map.insert(new_data.username.clone(), new_data);
            }

            format!(
                "HTTP/1.1 200 OK\r\n\r\n",
            )
        }
        Err(e) => {
            eprintln!("Error deserializing JSON: {}, {}", e, request);
            format!("HTTP/1.1 400 Bad Request\r\n\r\nError deserializing JSON.")
        }
    }
}

fn main() {
    let listener = TcpListener::bind("0.0.0.0:8080").unwrap();
    println!("Server listening on port 8080...");
    
    let map = Arc::new(RwLock::new(HashMap::<String, User>::new()));

    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                let cloned_map = map.clone();
                thread::spawn(|| handle_client(stream, cloned_map));
            }
            Err(e) => {
                println!("Error: {}", e);
            }
        }
    }
}
