export interface KafkaMessage<A> {
    headers: any
    key: {value: any, format: any}
    offset: number
    partition: number
    timestamp: Date
    topic: string
    value: A
}

export class SocketConnector<A> {
    constructor(
        wsBaseUrl: string,
        handleMessage: (msg: KafkaMessage<A>) => void) {
        const wsUrl = SocketConnector.buildUrl(wsBaseUrl)
        console.log("connecting to: ", wsUrl)
        const ws = new WebSocket(wsUrl)
        ws.onmessage = (msg) => handleMessage(JSON.parse(msg.data))
    }

    private static buildUrl(baseUrl: string): string {
        const clientId = 'cec'+crypto.randomUUID()
        return baseUrl + `/socket/out?clientId=${clientId}&topic=files&valType=json&autoCommit=false`
    }
}