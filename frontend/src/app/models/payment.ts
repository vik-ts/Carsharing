export class Payment {
    public id: number;
    public paymentRequisites: string;
    public amountToPay: number;
    public addAmountToPay: number;
    public confirmedUser: boolean;
    public confirmedAdmin: boolean;
    public rejectedAdmin: boolean;
    public closed: boolean;
    public comment: string;
}
