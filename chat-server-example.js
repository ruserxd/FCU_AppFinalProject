/**
 * WebSocket 聊天伺服器範例
 * 使用 Node.js 和 ws 套件
 * 
 * 安裝依賴：
 * npm install ws
 * 
 * 執行：
 * node chat-server-example.js
 * 
 * 伺服器將在 ws://localhost:8080 上運行
 */

const WebSocket = require('ws');

// 創建 WebSocket 伺服器
const wss = new WebSocket.Server({ port: 8080 });

// 儲存連接的客戶端
const clients = new Map(); // userId -> WebSocket
const rooms = new Map(); // roomId -> Set of userIds

console.log('聊天伺服器已啟動在 ws://localhost:8080');

wss.on('connection', (ws, req) => {
    console.log('新的客戶端連接');
    let currentUserId = null;
    let currentUserName = null;
    let currentRoomId = null;

    // 處理訊息
    ws.on('message', (message) => {
        try {
            const data = JSON.parse(message.toString());
            console.log('收到訊息:', data);

            switch (data.type) {
                case 'join':
                    // 用戶加入
                    currentUserId = data.userId;
                    currentUserName = data.userName || data.userId;
                    clients.set(currentUserId, ws);
                    
                    // 通知其他用戶
                    broadcastToAll({
                        type: 'user_joined',
                        userId: currentUserId,
                        userName: currentUserName
                    }, currentUserId);
                    
                    sendToClient(ws, {
                        type: 'joined',
                        message: '已成功連接到聊天伺服器'
                    });
                    break;

                case 'join_room':
                    // 加入聊天室
                    const roomId = data.roomId;
                    const roomName = data.roomName || roomId;
                    
                    // 離開之前的房間
                    if (currentRoomId && rooms.has(currentRoomId)) {
                        rooms.get(currentRoomId).delete(currentUserId);
                    }
                    
                    // 加入新房間
                    currentRoomId = roomId;
                    if (!rooms.has(roomId)) {
                        rooms.set(roomId, new Set());
                    }
                    rooms.get(roomId).add(currentUserId);
                    
                    // 通知房間內其他用戶
                    broadcastToRoom(roomId, {
                        type: 'user_joined_room',
                        userId: currentUserId,
                        userName: currentUserName,
                        roomId: roomId,
                        roomName: roomName
                    }, currentUserId);
                    
                    sendToClient(ws, {
                        type: 'room_joined',
                        roomId: roomId,
                        roomName: roomName
                    });
                    break;

                case 'leave_room':
                    // 離開聊天室
                    if (currentRoomId && rooms.has(currentRoomId)) {
                        rooms.get(currentRoomId).delete(currentUserId);
                        broadcastToRoom(currentRoomId, {
                            type: 'user_left_room',
                            userId: currentUserId,
                            userName: currentUserName,
                            roomId: currentRoomId
                        }, currentUserId);
                    }
                    currentRoomId = null;
                    break;

                case 'message':
                    // 聊天訊息
                    if (currentRoomId) {
                        broadcastToRoom(currentRoomId, {
                            type: 'message',
                            userId: currentUserId,
                            userName: currentUserName,
                            content: data.content,
                            roomId: currentRoomId,
                            timestamp: data.timestamp || Date.now()
                        }, currentUserId);
                    } else {
                        sendToClient(ws, {
                            type: 'error',
                            error: '請先加入聊天室'
                        });
                    }
                    break;

                case 'private_message':
                    // 私訊
                    const targetUserId = data.toUserId;
                    if (clients.has(targetUserId)) {
                        sendToClient(clients.get(targetUserId), {
                            type: 'private_message',
                            fromUserId: currentUserId,
                            fromUserName: currentUserName,
                            content: data.content,
                            timestamp: data.timestamp || Date.now()
                        });
                    } else {
                        sendToClient(ws, {
                            type: 'error',
                            error: '目標用戶不在線'
                        });
                    }
                    break;

                case 'leave':
                    // 用戶離開
                    if (currentRoomId && rooms.has(currentRoomId)) {
                        rooms.get(currentRoomId).delete(currentUserId);
                        broadcastToRoom(currentRoomId, {
                            type: 'user_left',
                            userId: currentUserId,
                            userName: currentUserName
                        }, currentUserId);
                    }
                    break;
            }
        } catch (error) {
            console.error('處理訊息錯誤:', error);
            sendToClient(ws, {
                type: 'error',
                error: '訊息格式錯誤'
            });
        }
    });

    // 處理斷開連接
    ws.on('close', () => {
        console.log('客戶端斷開連接:', currentUserId);
        
        if (currentUserId) {
            // 從房間中移除
            if (currentRoomId && rooms.has(currentRoomId)) {
                rooms.get(currentRoomId).delete(currentUserId);
                broadcastToRoom(currentRoomId, {
                    type: 'user_left',
                    userId: currentUserId,
                    userName: currentUserName
                }, currentUserId);
            }
            
            // 從客戶端列表中移除
            clients.delete(currentUserId);
            
            // 通知其他用戶
            broadcastToAll({
                type: 'user_left',
                userId: currentUserId,
                userName: currentUserName
            }, currentUserId);
        }
    });

    // 處理錯誤
    ws.on('error', (error) => {
        console.error('WebSocket 錯誤:', error);
    });
});

/**
 * 發送訊息給特定客戶端
 */
function sendToClient(ws, message) {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(message));
    }
}

/**
 * 廣播訊息給房間內的所有用戶（除了發送者）
 */
function broadcastToRoom(roomId, message, excludeUserId) {
    if (!rooms.has(roomId)) {
        return;
    }
    
    const roomUsers = rooms.get(roomId);
    roomUsers.forEach(userId => {
        if (userId !== excludeUserId && clients.has(userId)) {
            sendToClient(clients.get(userId), message);
        }
    });
}

/**
 * 廣播訊息給所有連接的客戶端（除了發送者）
 */
function broadcastToAll(message, excludeUserId) {
    clients.forEach((ws, userId) => {
        if (userId !== excludeUserId) {
            sendToClient(ws, message);
        }
    });
}

// 優雅關閉
process.on('SIGINT', () => {
    console.log('\n正在關閉伺服器...');
    wss.close(() => {
        console.log('伺服器已關閉');
        process.exit(0);
    });
});

