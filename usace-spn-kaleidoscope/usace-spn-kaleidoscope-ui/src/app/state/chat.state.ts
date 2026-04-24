import { v4 as uuidv4 } from 'uuid';
import { createReducer, on, createActionGroup, props, createFeatureSelector, createSelector } from '@ngrx/store';
import { ChatMessage } from '../models/chat.model';
import { MockUtil } from '../mock-util';
import { environment } from '../../environments/environment';

const initialMessage = ``;

const parseText = (m: ChatMessage): ChatMessage => {

    const message = { ...m }
    message.sections = [];

    const tokens = message.text
        // .replaceAll('\n', "<br/>")
        // .replaceAll('<br/><br/>', "<br/>")
        .split('<location>')

    let text = message.text;

    tokens.forEach(token => {

        const pattern = /<label>(.*)<\/label><uri>(.*)<\/uri><\/location>/

        if (pattern.test(token)) {
            const values = pattern.exec(token);
            const all: string = values?.at(0) as string;
            const label: string = values?.at(1) as string;
            const uri: string = values?.at(2) as string;

            text = text.replace(all, "[" + label + "](#/explorer/" + encodeURIComponent(uri) + ")");

            // // Ensure that the response contains a real URI and wasn't generated with some other
            // // if (uri.startsWith(environment.basePrefix)) {
            //     message.sections?.push({ type: 1, text: label, uri: uri })
            // // }
            // // else {
            // //     message.sections?.push({ type: 0, text: label, uri: uri })
            // // }

            // message.sections?.push({ type: 0, text: post })
        }
        // else {
        //     message.sections?.push({ type: 0, text: token })
        // }
    })

    message.sections?.push({ type: 0, text: text })


    return message;
}


export const ChatActions = createActionGroup({
    source: 'chat',
    events: {
        'Add Message': props<ChatMessage>(),
        'Update Message': props<ChatMessage>(),
        'setMessageAndSession': props<{ messages: ChatMessage[], sessionId: string }>(),
    },
});

export interface ChatStateModel {
    sessionId: string;
    messages: ChatMessage[];
}

export const initialState: ChatStateModel = {
    messages: [parseText({ id: '1', sender: 'system', text: initialMessage, mappable: false, ambiguous: false, purpose: 'info' })],
    sessionId: uuidv4()
}

// if (environment.mockRequests)
initialState.messages = MockUtil.messages.map(m => parseText(m));


export const chatReducer = createReducer(
    initialState,
    on(ChatActions.addMessage, (state, message) => {

        const messages = [...state.messages];
        messages.push(parseText(message));

        return { ...state, messages }
    }),
    on(ChatActions.updateMessage, (state, message) => {

        const messages = [...state.messages];

        const index = messages.findIndex(m => m.id === message.id)

        if (index != -1) {
            messages[index] = (parseText(message));
            return { ...state, messages }
        }

        return { ...state }
    }),
    on(ChatActions.setMessageAndSession, (state, wrapper) => {
        return { ...state, messages: wrapper.messages, sessionId: wrapper.sessionId }
    }),
);

const selector = createFeatureSelector<ChatStateModel>('chat');

export const getMessages = createSelector(selector, (s) => {
    return s.messages;
});

export const getSessionId = createSelector(selector, (s) => {
    return s.sessionId;
});
