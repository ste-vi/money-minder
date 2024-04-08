import { Component, Input } from '@angular/core';
import { Transaction } from '../../../models/transaction';
import {MatIcon} from "@angular/material/icon";
import {DatePipe, DecimalPipe, NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-transaction',
  standalone: true,
  imports: [MatIcon, DecimalPipe, DatePipe, NgIf, NgClass],
  templateUrl: './transaction.component.html',
  styleUrl: './transaction.component.scss',
})
export class TransactionComponent {
  @Input() transaction!: Transaction;
  @Input() isLast: boolean = false;

  constructor() {}
}
