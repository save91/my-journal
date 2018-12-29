const express = require('express')
const axios = require('axios')
const he = require('he')
const environment = require('./environment')
const PORT = 8080

const app = express()
const { serverAddress, serverPort, serverProtocol } = environment;

const baseUrl = `${serverProtocol}://${serverAddress}`
if (serverPort) {
    baseUrl.concat(':', serverPort)
}

const parseWordPressPost = (wpPost) => {
    return {
        title: he.decode(wpPost.title.rendered)
    }
}

app.get('/posts', async (req, res) => {
    const postsUrl = `${baseUrl}/wp-json/wp/v2/posts`;
    const responseFromWp = await axios.get(postsUrl)
    const toReturn = responseFromWp.data.map(post => parseWordPressPost(post))
    res.send(toReturn)
})

app.listen(PORT, () => {
    console.log(`Server in ascolto sulla porta: ${PORT}`)
})