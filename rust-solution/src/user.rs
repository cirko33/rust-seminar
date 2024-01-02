use serde::{Serialize, Deserialize};

#[derive(Serialize, Deserialize, Debug)]
pub struct User {
    pub username: String,
    pub pin: i64,
    pub zarada: f64,
}