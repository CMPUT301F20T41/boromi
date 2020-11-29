// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
import functions = require('firebase-functions')

// The Firebase Admin SDK to access Cloud Firestore.
import admin = require('firebase-admin')

admin.initializeApp()

export const notifyOnOwnerRequest = functions.firestore.document('/bookrequests/{documentId}').onCreate((snapshot) => {
	const bookRequestData = snapshot.data()
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

	functions.logger.log(`Sending incoming request notification to ${owner}`)

	return admin.messaging().sendToTopic(owner, payload, options)
})

export const notifyRequesterOnAcceptedRequest = functions.firestore.document('/books/{documentId}').onUpdate((snapshot) => {
	// Gets the state of the book before the change
	const prevBookData = snapshot.before.data()
	const prevStatus = prevBookData.status

	// Gets the state of the book after the change
	const bookData = snapshot.after.data()
	const {borrower, ownerName, status} = bookData

	// Validates that the change occurred in the status of the book, from requested to accepted
	if (prevStatus !== 'AVAILABLE' || status !== 'ACCEPTED') return null

	const payload = {
		notification: {
			title: 'Accepted Book Request',
			body: `${ownerName} accepted your book request`,
			sound: 'default',
		},
	}

	const options = {
		priority: "high",
		timeToLive: 60 * 60 * 24,
	};

	functions.logger.log(`Sending accepted request notification to ${borrower}`)

	// Sends a push notifcation to the borrower
	return admin.messaging().sendToTopic(borrower, payload, options)
})
