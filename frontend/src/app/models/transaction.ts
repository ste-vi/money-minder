import { Account } from './account';

export interface Transaction {
  id: number;
  name: string;
  amount: number;
  date: Date;
  account: Account;
}
