const APP_ID = "D718DE9B-58D6-449A-80A7-7AF34C6ABD1E";

const list = document.getElementById("chat-list");
const chatEl = document.getElementById("chat");
const buyerId = chatEl.dataset.buyerId;
const sellerId = chatEl.dataset.sellerId;
const dealId = chatEl.dataset.dealId;
const buyerName = chatEl.dataset.buyerName;
const sellerName = chatEl.dataset.sellerName;
const isSeller = chatEl.dataset.isSeller === "true";
const currentUserName = isSeller ? sellerName : buyerName;


//Log user
const currentUserId = isSeller ? sellerId : buyerId;
const otherUserId = isSeller ? buyerId : sellerId;
            

// init
const sb = new SendBird({ appId: APP_ID });

sb.connect(currentUserId, function(user, error) {
    if (error) {
        console.error("Connect error", error);
        return;
    }
            

    const params = new sb.GroupChannelParams();
        params.isDistinct = true;
        params.addUserIds([buyerId, sellerId]);
        params.name = "Deal " + dealId;
        params.channelUrl = "deal-" + dealId;

    sb.updateCurrentUserInfo(currentUserName, null, function(err) {
        if (err) console.error(err);
    });



    sb.GroupChannel.createChannel(params, function(channel, err2) {
        if (err2) {
            if (err2.code === 400201) { // channel_url already exists
                sb.GroupChannel.getChannel("deal-" + dealId, function(ch, err3) {
                    if (err3) { console.error(err3); return; }
                    startChat(ch);
                });
            } else {
                console.error("Create channel error", err2);
                return;
            }
        }
        startChat(channel);
    });

    function startChat(channel) {
        const list = document.getElementById("chat-list");
        const form = document.getElementById("chat-form");
        const input = document.getElementById("chat-input");
        const btn = document.getElementById("chat-send");


        function renderMessage(message) {
            const p = document.createElement("div");
            const isMe = message.sender && message.sender.userId === currentUserId;
            p.className = "msg" + (isMe ? " me" : "");
            p.textContent = (message.sender.nickname || message.sender.userId) + ": " + message.message;
            list.appendChild(p);
        }

        // upload messages
        const query = channel.createPreviousMessageListQuery();
        query.load(50, true, function(messages) {
            if (!messages) {
                console.error("No messages error");
            };
            messages.reverse().forEach(renderMessage);
        });

        // get new messages
        const handler = new sb.ChannelHandler();
        handler.onMessageReceived = function(ch, message) {
            if (ch.url === channel.url) {
                renderMessage(message);
            }
        };
        sb.addChannelHandler("deal-handler", handler);

        // send messages
        form.addEventListener("submit", function(e) {
            e.preventDefault();
            const text = input.value.trim();
            if (!text) return;
            channel.sendUserMessage(text, function(message, err) {
                if (!err) {
                    input.value = "";
                    renderMessage(message);
                }
            });
        });
    }
});