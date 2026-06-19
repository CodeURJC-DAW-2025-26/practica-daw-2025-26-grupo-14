import { useEffect, useState, useRef } from 'react'
import { useParams } from 'react-router-dom'
import { useAuthStore } from './stores/authStore'

declare global {
  interface Window {
    SendBird: any
  }
}

type Data = {
    buyerDni: number,
    buyerName: string,
    sellerDni: number,
    sellerName: string, 
    dealId: number, 
    productName: string
}

function SendBirdPage() {
    const { dealId } = useParams<{ dealId?: string }>()
    const userId = useAuthStore((state) => state.id)
    const [data, setData] = useState<Data | null>(null)
    const [messages, setMessages] = useState<any[]>([])
    const [messageText, setMessageText] = useState('')
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const messagesEndRef = useRef<HTMLDivElement>(null)
    const sbRef = useRef<any>(null)
    const channelRef = useRef<any>(null)

    const APP_ID = "D718DE9B-58D6-449A-80A7-7AF34C6ABD1E"

    useEffect(() => {
        if (!dealId) return

        // Load deal details
        fetch(`/api/v1/orders/${dealId}`, { credentials: 'include' })
            .then(res => {
                if (!res.ok) throw new Error('Failed to load deal')
                return res.json()
            })
            .then(async (deal) => {
                // Load buyer, product, and seller details
                const [buyerRes, productRes] = await Promise.all([
                    fetch(`/api/v1/users/${deal.buyerId}`, { credentials: 'include' }),
                    fetch(`/api/v1/products/${deal.productId}`, { credentials: 'include' })
                ])

                if (!buyerRes.ok || !productRes.ok) {
                    throw new Error('Failed to load buyer or product details')
                }

                const buyer = await buyerRes.json()
                const product = await productRes.json()

                // Load seller details
                const sellerRes = await fetch(`/api/v1/users/${product.sellerId}`, { credentials: 'include' })
                if (!sellerRes.ok) {
                    throw new Error('Failed to load seller details')
                }
                const seller = await sellerRes.json()

                const dealData: Data = {
                    buyerDni: deal.buyerId,
                    buyerName: buyer.name,
                    sellerDni: product.sellerId,
                    sellerName: seller.name,
                    dealId: deal.id,
                    productName: product.name
                }
                setData(dealData)
                initializeChat({ ...deal, buyer, product: { ...product, seller } })
            })
            .catch(err => {
                console.error(err)
                setError('Failed to load chat')
                setLoading(false)
            })
    }, [dealId])

    const initializeChat = (deal: any) => {
        const sb = new window.SendBird({ appId: APP_ID })
        sbRef.current = sb

        const isSeller = deal.product?.sellerId === userId
        const currentUserId = isSeller ? deal.product.sellerId.toString() : deal.buyerId.toString()
        const currentUserName = isSeller ? deal.product.seller.name : deal.buyer.name

        sb.connect(currentUserId, (_user: any, error: any) => {
            if (error) {
                console.error("Connect error", error)
                setError('Failed to connect to chat')
                setLoading(false)
                return
            }

            sb.updateCurrentUserInfo(currentUserName, null, (err: any) => {
                if (err) console.error(err)
            })

            const params = sb.GroupChannelParams()
            params.isDistinct = true
            params.addUserIds([deal.buyerId.toString(), deal.product.sellerId.toString()])
            params.name = `Deal ${deal.id}`
            params.channelUrl = `deal-${deal.id}`

            sb.GroupChannel.createChannel(params, (channel: any, err2: any) => {
                if (err2) {
                    if (err2.code === 400201) { // channel already exists
                        sb.GroupChannel.getChannel(`deal-${deal.id}`, (ch: any, err3: any) => {
                            if (err3) {
                                console.error(err3)
                                setError('Failed to join chat')
                                setLoading(false)
                                return
                            }
                            startChat(ch)
                        })
                    } else {
                        console.error("Create channel error", err2)
                        setError('Failed to create chat')
                        setLoading(false)
                        return
                    }
                } else {
                    startChat(channel)
                }
            })
        })
    }

    const startChat = (channel: any) => {
        channelRef.current = channel
        setLoading(false)

        // Load previous messages
        const query = channel.createPreviousMessageListQuery()
        query.load(50, true, (messages: any[]) => {
            if (messages) {
                setMessages(messages.reverse())
                scrollToBottom()
            }
        })

        // Listen for new messages
        const handler = new sbRef.current.ChannelHandler()
        handler.onMessageReceived = (ch: any, message: any) => {
            if (ch.url === channel.url) {
                setMessages(prev => [...prev, message])
                scrollToBottom()
            }
        }
        sbRef.current.addChannelHandler(`deal-handler-${channel.url}`, handler)
    }

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }

    const handleSendMessage = (e: React.FormEvent) => {
        e.preventDefault()
        const text = messageText.trim()
        if (!text || !channelRef.current) return

        channelRef.current.sendUserMessage(text, (_message: any, err: any) => {
            if (!err) {
                setMessageText('')
                // Message will be added via onMessageReceived
            } else {
                console.error('Send message error', err)
                setError('Failed to send message')
            }
        })
    }

    const renderMessage = (message: any) => {
        const isMe = message.sender && message.sender.userId === userId?.toString()
        return (
            <div key={message.messageId} className={`message ${isMe ? 'me' : ''}`}>
                <strong>{message.sender?.nickname || message.sender?.userId}:</strong> {message.message}
            </div>
        )
    }

    if (loading) {
        return <div className="container mt-4"><div className="text-center">Loading chat...</div></div>
    }

    if (error) {
        return <div className="container mt-4"><div className="alert alert-danger">{error}</div></div>
    }

    return (
        <>
            <h1 style={{ textAlign: 'center' }}>Chat about {data?.productName}</h1>
            <p style={{ textAlign: 'center' }}>State your conditions</p>
            <div className="chat-card"
                 id="chat"
                 data-buyer-id={data?.buyerDni}
                 data-seller-id={data?.sellerDni}
                 data-deal-id={data?.dealId}
                 data-buyer-name={data?.buyerName}
                 data-seller-name={data?.sellerName}
                 data-is-seller={data?.sellerDni === userId ? "true" : "false"}>

            <div id="chat-list" className="chat-list">
                {messages.map(renderMessage)}
                <div ref={messagesEndRef} />
            </div>
            <form id="chat-form" className="chat-form" onSubmit={handleSendMessage}>
                <input id="chat-input" type="text" placeholder="Message..." value={messageText} onChange={(e) => setMessageText(e.target.value)} />
                <button id="chat-send" type="submit">Send</button>
            </form>
            </div>
        </>
    )
}

export default SendBirdPage
