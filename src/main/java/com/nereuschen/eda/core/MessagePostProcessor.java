/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nereuschen.eda.core;

import com.nereuschen.eda.Message;

/**
 * To be used with MessagingTemplate's send method that converts an object to a message.
 * It allows for further modification of the message after it has been processed
 * by the converter.
 *
 * <p>This is often implemented as an anonymous class within a method implementation.
 *
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 * @since 2.0
 * @see MessagingTemplate#convertAndSend(String, Object, MessagePostProcessor)
 * @see MessagingTemplate#convertAndSend(org.springframework.integration.MessageChannel, Object, MessagePostProcessor)
 * @see org.springframework.integration.support.converter.MessageConverter
 */
public interface MessagePostProcessor {

	/**
	 * Apply a MessagePostProcessor to the message. The returned message is
	 * typically a modified version of the original.
	 * @param message the message returned from the MessageConverter
	 * @return the modified version of the Message
	 */
	Message<?> postProcessMessage(Message<?> message);

}
