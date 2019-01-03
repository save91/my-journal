const express = require('express')
const axios = require('axios')
const he = require('he')
const environment = require('./environment')
const PORT = 8080
const GTM = '000Z'

const app = express()
const { serverAddress, serverPort, serverProtocol } = environment;

const baseUrl = `${serverProtocol}://${serverAddress}`
if (serverPort) {
    baseUrl.concat(':', serverPort)
}

const parseWordPressPost = (wpPost) => {
    const data = new Date(`${wpPost.date_gmt}.${GTM}`);
    const modified = new Date(`${wpPost.modified_gmt}.${GTM}`);

    return {
        id: wpPost.id,
        author_id: wpPost.author,
        body: wpPost.content.rendered,
        categories_id: wpPost.categories,
        date: data.getTime(),
        featured_media_id: wpPost.featured_media,
        link: wpPost.link,
        modified: modified.getTime(),
        tags_id: wpPost.tags,
        title: he.decode(wpPost.title.rendered)
    }
}

app.get('/api/:version/posts', async (req, res) => {
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts`;
    const responseFromWp = await axios.get(postsUrl)
    const toReturn = responseFromWp.data.map(post => parseWordPressPost(post))
    res.send(toReturn)
})

app.listen(PORT, () => {
    console.log(`Server in ascolto sulla porta: ${PORT}`)
})