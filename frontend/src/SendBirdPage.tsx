import { useEffect, useState, useRef } from 'react'
import { useParams } from 'react-router-dom'
import { useAuthStore } from './stores/authStore'
import SendBird from 'sendbird'


type ConversationChannel = {
    channel_url: string
    name: string
    members: any[]
}

function SendBirdPage() {
    const { orderId } = useParams<{ orderId: string }>()
    const userId = useAuthStore((state) => state.id)
    const [userDni, setUserDni] = useState<string | null>(null)

    const [channelData, setChannelData] = useState<ConversationChannel | null>(null)
    const [messages, setMessages] = useState<any[]>([])
    const [messageText, setMessageText] = useState('')
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const messagesEndRef = useRef<HTMLDivElement>(null)
    const sbRef = useRef<any>(null)
    const channelRef = useRef<any>(null)

    const APP_ID = "D718DE9B-58D6-449A-80A7-7AF34C6ABD1E"


    // 1. Load conversation list + find channel
    useEffect(() => {
        if (!orderId || !userId) {
            setLoading(false)
            setError('Missing order or user information')
            return
        }

        fetch(`/api/v1/users/${userId}/conversations`, {
            credentials: 'include'
        })
            .then(res => {
                if (!res.ok) throw new Error('Failed to load conversations')
                return res.json()
            })
            .then((data) => {
                const channels: ConversationChannel[] = data.channels || data

                const channel = channels.find(
                    (c) => c.channel_url === "deal-" +  orderId
                )

                if (!channel) {
                    throw new Error('Chat not found')
                }

                setChannelData(channel)
                initializeChat(channel)
            })
            .catch(err => {
                setError('error loading chat: ' + err.message)
            })
            .finally(() => {
                setLoading(false)
            })
    }, [orderId, userId])

    // 2. Init SendBird connection + open channel
    const initializeChat = (channel: ConversationChannel) => {
    const sb = new SendBird({ appId: APP_ID })
    sbRef.current = sb

    fetch(`/api/v1/users/${userId}`, {
            credentials: 'include'
        })
    .then(res => {
        if (!res.ok) throw new Error('Failed to load user')
        return res.json()
    })
    .then((user) => {
        setUserDni(user.dni) 

        sb.connect(user.dni.toString(), (_user, error) => {
        if (error) {
            setError('Failed to connect to chat')
            setLoading(false)
            return
        }

    })
    .catch(err => {
        setError('Failed to load user information:' + err.message)
    })

        sb.GroupChannel.getChannel(channel.channel_url, (ch, err) => {
            if (err) {
                setError('Failed to load channel')
                setLoading(false)
                return
            }

            startChat(ch)
        })
    })
}

    // 3. Start chat (messages + listener)
    const startChat = (channel: any) => {
        channelRef.current = channel
        setLoading(false)

        // Load previous messages
        const query = channel.createPreviousMessageListQuery()
        query.load(50, true, (msgs: any[]) => {
            if (msgs) {
                setMessages(msgs.reverse())
                scrollToBottom()
            }
        })

        // Listen new messages
        const handler = new sbRef.current.ChannelHandler()

        handler.onMessageReceived = (ch: any, message: any) => {
            if (ch.url === channel.url) {
                setMessages(prev => [...prev, message])
                scrollToBottom()
            }
        }

        sbRef.current.addChannelHandler(
            `chat-handler-${channel.url}`,
            handler
        )
    }

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }

    // 4. Send message
    const handleSendMessage = (e: React.FormEvent) => {
        e.preventDefault()

        const text = messageText.trim()
        if (!text || !channelRef.current) return

        channelRef.current.sendUserMessage(text, (_msg: any, err: any) => {
            if (err) {
                setError('Failed to send message:' + err.message)
                return
            }

            setMessageText('')
        })
    }

    const renderMessage = (message: any) => {
        const isMe =
            message.sender &&
            message.sender.userId === userDni?.toString()

        return (
            <div
                key={message.messageId}
                className={`message ${isMe ? 'me' : ''}`}
            >
                <strong>
                    {message.sender?.nickname || message.sender?.userId}:
                </strong>{' '}
                {message.message}
            </div>
        )
    }

    if (loading) {
        return (
            <div className="container mt-4">
                <div className="text-center">Loading chat...</div>
            </div>
        )
    }

    if (error) {
        return (
            <div className="container mt-4">
                <div className="alert alert-danger">{error}</div>
            </div>
        )
    }

    return (
        <>
            <h1 style={{ textAlign: 'center' }}>
                {channelData?.name || 'Chat'}
            </h1>

            <div className="chat-card" id="chat">
                <div id="chat-list" className="chat-list">
                    {messages.map(renderMessage)}
                    <div ref={messagesEndRef} />
                </div>

                <form
                    id="chat-form"
                    className="chat-form"
                    onSubmit={handleSendMessage}
                >
                    <input
                        id="chat-input"
                        type="text"
                        placeholder="Message..."
                        value={messageText}
                        onChange={(e) => setMessageText(e.target.value)}
                    />
                    <button id="chat-send" type="submit">
                        Send
                    </button>
                </form>
            </div>
        </>
    )
}

export default SendBirdPage
