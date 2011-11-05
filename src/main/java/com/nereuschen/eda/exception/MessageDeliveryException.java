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

package com.nereuschen.eda.exception;

import com.nereuschen.eda.message.Message;

/**
 * Exception that indicates an error occurred during message delivery.
 * 
 * @author Mark Fisher
 * @author Nereus Chen (nereus.chen@gmail.com)
 */
@SuppressWarnings("serial")
public class MessageDeliveryException extends MessagingException {
	
	public MessageDeliveryException(String description) {
		super(description);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage) {
		super(undeliveredMessage);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage, String description) {
		super(undeliveredMessage, description);
	}

	public MessageDeliveryException(Message<?> undeliveredMessage, String description, Throwable cause) {
		super(undeliveredMessage, description, cause);
	}

}
