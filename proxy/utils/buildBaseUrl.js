const buildBaseUrl = ({ serverAddress, serverPort, serverProtocol }) => {
    let toReturn = `${serverProtocol}://${serverAddress}`
    if (serverPort) {
        toReturn += `:${serverPort}`
    }

    console.log('buildBaseUrl: ', toReturn)
    return toReturn
}

module.exports = buildBaseUrl