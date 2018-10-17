const express = require('express')
const axios = require('axios')
const PORT = 8080

const app = express()

const parseWordPressPost = (wpPost) => {
    return {
        title: wpPost.title.rendered
    }
}

app.get('/posts', async (req, res) => {
    const responseFromWp = await axios.get('http://localhost/wp-json/wp/v2/posts')
    const toReturn = responseFromWp.data.map(post => parseWordPressPost(post))
    res.send(toReturn)
})

app.listen(PORT, () => {
    console.log(`Server in ascolto sulla porta: ${PORT}`)
})