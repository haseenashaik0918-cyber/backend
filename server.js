const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose");
const path = require("path");

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, "..", "frontend")));

const taskRoutes = require("./routes/taskRoutes");
const authRoutes = require("./routes/authRoutes");

app.get("/", (req, res) => {
    res.sendFile(path.join(__dirname, "..", "frontend", "pages", "login.html"));
});

app.use("/tasks", taskRoutes);
app.use("/auth", authRoutes);

mongoose.connect("mongodb://127.0.0.1:27017/TaskManagementDB")
    .then(() => {
        console.log("MongoDB Connected Successfully");

        app.listen(5001, () => {
            console.log("Server running on port 5001");
        });
    })
    .catch((err) => {
        console.error("MongoDB connection failed:", err.message);
        process.exit(1);
    });