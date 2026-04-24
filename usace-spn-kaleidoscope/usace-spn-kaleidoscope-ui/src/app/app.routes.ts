import { Routes } from '@angular/router';

import { ExplorerComponent } from './explorer/explorer.component';

export const routes: Routes = [
  { path: 'explorer', component: ExplorerComponent },
  { path: 'explorer/:uri', component: ExplorerComponent },
  { path: '', redirectTo: '/explorer', pathMatch: 'full' } // default route

];
