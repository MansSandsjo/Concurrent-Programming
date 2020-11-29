package chat.test;

import org.junit.Before;
import org.junit.Test;

import chat.client.ChatLog;
import chat.client.ServerControl;
import chat.client.Twit;

public class testManyChats {

	public testManyChats() {
		class ChatTest {

			

			@Test
			void testManyTwits() throws InterruptedException {
				final int NBR_TWITS = 12; // number of clients
				final int NBR_MESSAGES = 30000; // number of messages from each
												// client
				final int MESSAGE_DELAY = 0; // maximal delay between messages

				for (int i = 1; i <= NBR_TWITS; i++) {
					Twit t = new Twit("twit" + i, NBR_MESSAGES, MESSAGE_DELAY);
					t.start();
				}

				ChatLog.expect(NBR_TWITS, NBR_TWITS * NBR_MESSAGES);
			}
		}
	}
}
