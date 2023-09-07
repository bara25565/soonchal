import './App.css';
import React, { useState, useEffect } from 'react';

function App() {
    const [message, setMessage] = useState(null);

    useEffect(() => {
        fetch("/schedule")
            .then((res) => res.json())
            .then((data) => {
                setMessage(data);
                console.log(data);
            });
    }, []);

    return (
        <div className="App">
            교내순찰위원회 순찰표
            { message && message.schedule && JSON.stringify(message.schedule[0].id) }
        </div>
    );
}

export default App;
