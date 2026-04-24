import { ApplicationConfig } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, withHashLocation } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideStore } from '@ngrx/store';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura';

import { routes } from './app.routes';
import { chatReducer } from './state/chat.state';
import { MessageService } from 'primeng/api';
import { explorerReducer } from './state/explorer.state';
import { authInterceptor } from './service/auth-interceptor.service';
import { provideMarkdown } from 'ngx-markdown';

export const appConfig: ApplicationConfig = {
    providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideRouter(routes, withHashLocation()),
        provideAnimations(),
        provideAnimationsAsync(),
        provideMarkdown(),
        providePrimeNG({
            theme: {
                preset: Aura
            }
        }),
        provideStore({
            chat: chatReducer,
            explorer: explorerReducer
        }),
        MessageService,
    ]
};