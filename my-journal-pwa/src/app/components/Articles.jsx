import React from 'react';
import { ArticleListItem } from './ArticleListItem.jsx';
import posts from '../mocks/posts';

export function Articles() {
    return posts.map(p => (<ArticleListItem key={p.id} title={p.title} img={p.featured_media.thumbnail_url}></ArticleListItem>));
}
