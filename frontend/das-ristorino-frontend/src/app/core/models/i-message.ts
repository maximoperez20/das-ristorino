export interface IMessage {
  text: string;
  num?: number;
  title?: string;
  type?: 'success' | 'error' | 'info' | 'warning';
}
