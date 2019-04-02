import React from 'react';

export function ArticleListItem(props) {
    const { title, img } = props;
    return (<>
        <img src={ img }></img>
        <h1>{ title }</h1>
        <hr/>
    </>);
}
