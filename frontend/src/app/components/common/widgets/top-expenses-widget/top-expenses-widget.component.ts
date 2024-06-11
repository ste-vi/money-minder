import { Component } from '@angular/core';
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-top-expenses-widget',
  standalone: true,
  imports: [
    DatePipe
  ],
  templateUrl: './top-expenses-widget.component.html',
  styleUrl: './top-expenses-widget.component.scss'
})
export class TopExpensesWidgetComponent {

    protected readonly currentDate: Date = new Date();
}
