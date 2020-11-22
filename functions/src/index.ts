// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
import functions = require('firebase-functions')

// The Firebase Admin SDK to access Cloud Firestore.
import admin = require('firebase-admin')

admin.initializeApp()

interface BookRequest {
	bookId: string
	owner: string
	requestDate: Date
	requestId: string
	requestor: string
	requestorName: string
}

export const notifyOnOwnerRequest = functions.firestore.document('/bookrequests/{documentId}').onCreate((snapshot, context) => {
	const documentId = context.params.documentId

	console.log(`New Book Request was placed: ${documentId}`)

	const bookRequestData: BookRequest = <BookRequest> snapshot.data()
	const { requestorName, owner } = bookRequestData

	const payload = {
		notification: {
			title: 'Incoming Book Request',
			body: `${requestorName} requested to borrow one of your books`,
			sound: 'default',
		},
	}

	const options = {
		priority: "high",
		timeToLive: 60 * 60 * 24,
	};

	return admin.messaging().sendToTopic(owner, payload, options)
})
