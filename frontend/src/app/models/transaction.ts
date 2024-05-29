import {Account} from './account';
import {Currency} from "./currency";

export interface Transaction {
  id: string;
  name: string;
  amount: number;
  currency: Currency;
  date: Date;
  fromAccount: Account;
  toAccount: Account;
  notes?: string;
  type: TransactionType,
  isBankTransaction: boolean;
}

export enum TransactionType {
  EXPENSE = "EXPENSE",
  INCOME = "INCOME",
}
