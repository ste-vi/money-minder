import { Currency } from './currency';

export interface Account {
  id: number;
  name: string;
  balance: number;
  currency: Currency;
}
