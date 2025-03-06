import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './components/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HomeComponent,HeaderComponent],
  template: `
    <app-home/>
    <app-header/>
  `,
  styles: [],
})
export class AppComponent {
  title = 'first-ng-app';
}
