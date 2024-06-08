import {Component} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {CreateTransactionService} from "../../../../services/communication/create-transaction-service";

@Component({
  selector: 'app-create-transaction-button',
  standalone: true,
  imports: [
    MatIcon
  ],
  templateUrl: './create-transaction-button.component.html',
  styleUrl: './create-transaction-button.component.scss'
})
export class CreateTransactionButtonComponent {

  constructor(private createTransactionService: CreateTransactionService) {
  }

  openCreateTransactionModal() {
    this.createTransactionService.openModal();
  }
}
