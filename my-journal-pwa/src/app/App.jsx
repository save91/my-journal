import React from 'react';
import ReactDOM from "react-dom";
import { Header } from "./components/Header.jsx"
import { Articles } from "./components/Articles.jsx";

function App() {
    return (<>
        <Header></Header>
        <Articles></Articles>
    </>);
}

ReactDOM.render(
    <App />,
    document.getElementById('app')
);
