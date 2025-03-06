- Single Page Application
- Build by Google

- Faster Developement
- Fast code generation
- Unit Test Ready
- Code Reusability

- is a framework
- built in CLI
- has tools and packages included


### Framework
- Framework generally contains set of libraries
- You can extends the frameworks

### Libraries
- generally perform specific operations
- You can combine multiple libs to build apps
- Example : React, Lodash

CORE Concepts
- Template
- Components, Services
- Directives, Pipes
- Data-Binding, Event Handlers
- Http Modules, Forms Module
- Routing, Animations
- Testing and Production
- Observales
- PWA

### SPA
- SPA stands for Single Page Applications.
- You can use modern frameworks like Angular, React or Vue to create SPA.
- SPA does not make requests to server for every URL requests.
- Angular has Routing functionality to create SPA.
- Angular also offers SSR(Server Side Rendering) which supports SPA.

## Angular Data-Bining

- Data binding in Angular is a mechanism that allows you to synchronize data between the component (TypeScript) and the view (HTML). Angular provides four types of data binding, which together form full data binding.

1. Interpolation (One-Way Binding - Component to View)
- Interpolation is the simplest form of data binding where you embed expressions into the template using double curly braces {{ }}.
```typescript []
TS:
export class AppComponent {
  message: string = "Hello, Angular!";
}
```

```html
HTML:
<h1>{{ message }}</h1>  <!-- Output: Hello, Angular! -->
```

🔹 Use Case: Displaying dynamic values in the UI.

2. Property Binding (One-Way Binding - Component to View)
Property binding is used to bind a property of **an HTML element** to a value from the component.

Example:
```typescript
export class AppComponent {
  imageUrl: string = "https://angular.io/assets/images/logos/angular/angular.png";
}
```
```html
<img [src]="imageUrl" />
```
🔹 Use Case: Dynamically setting properties of elements.

3. Event Binding (One-Way Binding - View to Component)
Event binding allows the template (view) to send events to the component.

Example:
typescript

```typescript
export class AppComponent {
  count: number = 0;

  increment() {
    this.count++;
  }
}
```
```html
<button (click)="increment()">Click Me</button>
<p>Count: {{ count }}</p>
```
🔹 Use Case: Handling user interactions like clicks, keypresses, and form submissions.

4. Two-Way Binding (Two-Way Sync - Component <-> View)
Two-way binding allows synchronization between the component and the view using the [(ngModel)] directive.

```typescript
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  name: string = "";
}
```
```html
<input [(ngModel)]="name" placeholder="Enter your name" />
<p>Hello, {{ name }}!</p>
```
🔹 Use Case: Forms and user inputs where data should be in sync.

### Data Binding with Signals